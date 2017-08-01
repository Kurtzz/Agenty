LearningSet = Struct.new(:description, :set) do
  def inspect
    description
  end

  def slug
    description.downcase.strip.gsub(' ', '-').gsub(/[^\w-]/, '')
  end
end

class LearningSets
  def self.simple(group_cardinality, multiply, base = (0..9).to_a, description = "Simple cardinatlity: #{group_cardinality} * #{multiply}")
    set = (group_cardinality - 1).times.reduce(base.dup) do |obj,i|
      obj.zip(base.rotate(i+1)).map(&:flatten)
    end * multiply
    set = base.dup if group_cardinality == 1

    LearningSet.new(description, set)
  end

  def self.get
    [
      # LearningSet.new('Reference', [(0..9).to_a]),
      # LearningSet.new('6 groups each 5 numbers randomly', [[9, 2, 4, 1, 6], [6, 7, 3, 8, 4], [0, 9, 5, 3, 8], [1, 9, 0, 7, 3], [4, 0, 5, 1, 2], [7, 8, 2, 5, 6]]),
      LearningSet.new('6 groups each 5 numbers 01234 56789', [[0, 1, 2, 3, 4], [5, 6, 7, 8, 9], [0, 1, 2, 3, 4], [5, 6, 7, 8, 9], [0, 1, 2, 3, 4], [5, 6, 7, 8, 9]])
      # simple(1, 2),
      # simple(2, 1),
      # simple(2, 2),
      # simple(3, 1),
      # simple(3, 2),
      # simple(4, 1),
      # simple(4, 2),
      # simple(5, 1),
      # simple(5, 2),
      # simple(3, 2, (0..9).to_a - [2], 'Without 2: cardinatlity: 3 * 2'),
      # LearningSet.new('6 twice * 2', [(0..6).to_a, (6..9).to_a]),
      # LearningSet.new('Round separately * 2', [[1,4,7], [2,3,5,6,8,9]] * 2),
      # LearningSet.new('Reference', [(0..9).to_a]),
    ]
  end
end
