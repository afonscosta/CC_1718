max=10000000
for i in `seq 0 $max`
do
   wget 10.4.4.20
   rm index.html
done
