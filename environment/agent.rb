class Agent
  attr_reader :container, :service_port
  def initialize(container, service_port)
    @container = container
    @service_port = service_port
  end

  def endpoint
    "localhost:#{service_port}"
  end
end
