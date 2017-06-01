$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require_relative 'keeper.rb'
require_relative 'agent.rb'
require_relative 'central_agent.rb'

require 'pry'

LEARNING_AGENT_IMAGE_NAME = 'learning_agent'.freeze

keeper = Keeper.new(Agent, LEARNING_AGENT_IMAGE_NAME, 5)
keeper.up

binding.pry


keeper.down