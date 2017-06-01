class CentralAgent < Agent
  def service
    @stub ||= CentralAgent::Stub.new(endpoint, :this_channel_is_insecure)
  end
end
