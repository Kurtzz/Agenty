require 'zlib'
# https://github.com/gbuesing/mnist-ruby-test/blob/master/train/mnist_loader.rb
class MnistLoader

  def self.training_set
    new 'mnist_data/train-images-idx3-ubyte.gz', 'mnist_data/train-labels-idx1-ubyte.gz'
  end

  def self.test_set
    new 'mnist_data/t10k-images-idx3-ubyte.gz', 'mnist_data/t10k-labels-idx1-ubyte.gz'
  end

  def initialize images_file, labels_file
    @images_file = images_file
    @labels_file = labels_file

    unless File.exist?(@images_file) && File.exist?(@labels_file)
      raise "Missing MNIST datafiles\nDownload from: http://yann.lecun.com/exdb/mnist/"
    end
  end

  def get_data_and_labels digits
    load_data
    @data.select { |r| digits.include?(r[:targetInt]) }
    # @data.shuffle!
    # x_data, y_data = [], []
    
    # @data.slice(0, size).each do |row|
    #   image = row[0].unpack('C*')
    #   image = image.map {|v| normalize(v, 0, 256, 0, 1)}
    #   x_data << image
    #   y_data << row[1]
    # end

    # [x_data, y_data]
  end

  private
    def load_data
      return if @data
      n_rows = n_cols = nil
      images = []
      labels = []
      Zlib::GzipReader.open(@images_file) do |f|
        magic, n_images = f.read(8).unpack('N2')
        raise 'This is not MNIST image file' if magic != 2051
        n_rows, n_cols = f.read(8).unpack('N2')
        n_images.times do
          images << f.read(n_rows * n_cols)
        end
      end

      Zlib::GzipReader.open(@labels_file) do |f|
        magic, n_labels = f.read(8).unpack('N2')
        raise 'This is not MNIST label file' if magic != 2049
        labels = f.read(n_labels).unpack('C*')
      end

      # collate image and label data
      @data = images.map.with_index do |image, i|
        { image: image, targetInt: labels[i] }
      end
    end

    # def normalize val, fromLow, fromHigh, toLow, toHigh
    #   (val - fromLow) * (toHigh - toLow) / (fromHigh - fromLow).to_f
    # end
end
