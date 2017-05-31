class Agent
  attr_reader :container, :service_port
  def initialize(container, service_port)
    @container = container
    @service_port = service_port
  end

  def service
    @stub ||= LearningAgent::Stub.new("localhost:#{service_port}", :this_channel_is_insecure)
  end
end