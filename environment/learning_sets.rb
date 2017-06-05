LearningSet = Struct.new(:description, :set) do
  def inspect
    description
  end
end

class LearningSets
  def self.simple(group_cardinality, multiply, base = (0..9).to_a, description = "Simple cardinatlity: #{group_cardinality} * #{multiply}")
    set = group_cardinality.times.reduce(base.dup) do |obj,i|
      obj.zip(base.rotate(i+1)).map(&:flatten)
    end * multiply
    LearningSet.new(description, set)
  end

  def self.get
    [
      LearningSet.new('Reference', (0..9).to_a),
      simple(1, 3),
      simple(2, 1),
      simple(2, 3),
      simple(3, 1),
      simple(3, 3),
      simple(4, 1),
      simple(4, 2),
      simple(5, 1),
      simple(5, 2),
      simple(3, 2, (0..9).to_a - [2], 'Without 2: cardinatlity: 3 * 2'),
      LearningSet.new('6 twice * 2', [(0..6).to_a, (6..9).to_a]),
      LearningSet.new('Round separately * 2', [[1,4,7], [2,3,5,6,8,9]] * 2)
    ]
  end
end