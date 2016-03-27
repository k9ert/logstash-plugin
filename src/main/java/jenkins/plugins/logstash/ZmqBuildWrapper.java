/*
 * The MIT License
 *
 * Copyright 2013 Hewlett-Packard Development Company, L.P.
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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;
import java.io.OutputStream;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build wrapper that decorates the build's logger to insert a
 * {@link LogstashNote} on each output line.
 *
 * @author K Jonathan Harker
 */
public class ZmqBuildWrapper extends BuildWrapper {

  /**
   * Create a new {@link ZmqBuildWrapper}.
   */
  @DataBoundConstructor
  public ZmqBuildWrapper() {}

  /**
   * {@inheritDoc}
   */
  @Override
  public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

    return new Environment() {};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OutputStream decorateLogger(AbstractBuild build, OutputStream logger) {
    return new ZmqOutputStream(logger, build);
  }

  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }


  /**
   * Registers {@link ZmqBuildWrapper} as a {@link BuildWrapper}.
   */
  @Extension
  public static class DescriptorImpl extends BuildWrapperDescriptor {

    public DescriptorImpl() {
      super(ZmqBuildWrapper.class);
      load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
      return Messages.DisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return true;
    }
  }
}
