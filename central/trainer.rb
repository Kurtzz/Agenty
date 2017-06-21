$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'

class Trainer
  def initialize(stub, learning_set)
    @stub = stub
    @learning_set = learning_set
  end

  def train
    imgs = @learning_set.map { |r| r[:image] }.join
    labels = @learning_set.map {|r| r[:targetInt]}.pack('C*')
    @stub.reset(ResetRequest.new)
    @stub.train_batch(TrainBatchRequest.new(count: @learning_set.count, labels: labels, pixels: imgs))
  end
end