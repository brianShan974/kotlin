mv test.kt.orig test.kt
creduce --save-temps --not-c --debug --n 1 ./test.sh test2.kt | tee result.txt
