set terminal png
set output filename.'.png'

set xrange [-1:11]
set yrange [:100]

set xlabel 'Digit'
set ylabel '% of correct answers'

set key right bottom

plot filename.'.dat' using 1:(100* $2) with points title 'probablity sum', filename.'.dat' using 1:(100* $3) with points title 'softmax voting'
