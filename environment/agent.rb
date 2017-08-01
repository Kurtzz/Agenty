class Agent
  attr_reader :ip, :service_port
  def initialize(ip, service_port)
    @ip = ip
    @service_port = service_port
  end

  def endpoint
    "#{ip}:#{service_port}"
  end
end
