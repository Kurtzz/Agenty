require 'docker'
require_relative 'agent.rb'
require 'pry'

class Keeper
  IMAGE_NAME='learning_agent'.freeze
  attr_reader :agents
  def initialize(image_name, nodes_count, start_port = 7000)
    @image_name = image_name
    @nodes_count = nodes_count
    @start_port = start_port
  end

  def up
    @agents ||= @nodes_count.times.map do |i|
      service_port = @start_port + i + 1
      container = Docker::Container.create(
        'name' => "learning_agent_#{i+1}",
        'Image' => @image_name,
        'ExposedPorts' => { '6565/tcp' => {} },
        'HostConfig' => {
          'PortBindings' => {
            '6565/tcp' => [{ 'HostPort' => service_port.to_s }]
          },
          'Binds' => [
            "#{Dir.home}/.m2:/root/.m2"
          ]
        }
      )
      container.start
      Agent.new(container, service_port)
    end
  end

  def ips
    @ips ||= @agents.map { |c| c.container.json["NetworkSettings"]["IPAddress"] }
  end

  def down
    @agents.each do |c|
      c.container.delete(force: true)
    end
  end
end