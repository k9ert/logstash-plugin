/*
 * The MIT License
 *
 * Copyright 2014 K Jonathan Harker & Rusty Gerard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.logstash;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.console.ConsoleNote;
import hudson.console.LineTransformationOutputStream;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import jenkins.model.Jenkins;

import org.zeromq.ZMQ;

/**
 * Output stream that writes each line to the provided delegate output stream
 * and also sends it to an indexer for logstash to consume.
 *
 * @author K Jonathan Harker
 * @author Rusty Gerard
 */
public class ZmqOutputStream extends LineTransformationOutputStream {
  final OutputStream delegate;
  String zmqPrefix;
  private static ZMQ.Socket zmqPublisher = null;

  private static ZMQ.Socket getZmqPublisher() {
	  if (zmqPublisher == null) {
		  ZMQ.Context zmqContext = ZMQ.context(1);
		  zmqPublisher = zmqContext.socket(ZMQ.PUB);
		  zmqPublisher.bind("tcp://*:5556");
	  }
	  return zmqPublisher;
  }
  
  public ZmqOutputStream(OutputStream delegate, AbstractBuild build) {
    super();
    this.delegate = delegate;
    Binding binding = new Binding();
    binding.setVariable("build", build);
    binding.setVariable("jenkins",Jenkins.getInstance());

    GroovyShell shell = new GroovyShell(binding); 
    zmqPrefix = (String)shell.evaluate(LogstashInstallation.getLogstashDescriptor().zmqprefix);
  }

  @Override
  protected void eol(byte[] b, int len) throws IOException {
    delegate.write(b, 0, len);
    this.flush();

    String line = ( zmqPrefix + " " + new String(b, 0, len)).trim();
    line = ConsoleNote.removeNotes(line);
    getZmqPublisher().send(line, 0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void flush() throws IOException {
    delegate.flush();
    super.flush();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {
    delegate.close();
    super.close();
  }
}
