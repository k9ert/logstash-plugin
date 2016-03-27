#!/usr/bin/env ruby

#
# Weather update client in Ruby
# Connects SUB socket to tcp://localhost:5556
# Collects weather updates and finds avg temp in zipcode
#

require 'rubygems'
require 'ffi-rzmq'

require 'statsd-ruby'

COUNT = 100

context = ZMQ::Context.new(1)

statsd = Statsd.new 'some.statsd.server', 8125

# Socket to talk to server
puts "Collecting logs from jenkins server..."
subscriber = context.socket(ZMQ::SUB)
subscriber.connect("tcp://localhost:5556")

# We need to subscribe to something
# let's say anything:
subscriber.setsockopt(ZMQ::SUBSCRIBE,"")


counter = 0
time = Time.now
while(true)
  s = ''
  if ((Time.now - time) > 1)
    statsd.count("jenkins.loglines.processed",counter)
    puts "sent" + counter.to_s()
    time = Time.now
    counter = 0
  end
  subscriber.recv_string(s)
  puts s
  counter= counter + 1
end
