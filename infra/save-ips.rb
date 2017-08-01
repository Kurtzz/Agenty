#!/usr/bin/env ruby

require 'json'

tfstate = JSON(File.read('terraform.tfstate'))
resources = tfstate['modules'][0]['resources']

learning_agents_ips = resources
	.select { |k,_| k.start_with?('google_compute_instance.learning-agent') }
	.map { |_,o| o['primary']['attributes']['network_interface.0.address'] }

File.write('../environment/learning-agents-ips.txt', learning_agents_ips.join("\n"))

central_agent_ip = resources
	.select { |k,_| k.start_with?('google_compute_instance.central-agent') }
	.map { |_,o| o['primary']['attributes']['network_interface.0.address'] }

File.write('../environment/central-agent-ip.txt', central_agent_ip.join("\n"))

env_agent_ip = resources
	.select { |k,_| k.start_with?('google_compute_instance.environment-agent') }
	.map { |_,o| o['primary']['attributes']['network_interface.0.access_config.0.nat_ip'] }

puts "ssh ubuntu@#{env_agent_ip.join}"
