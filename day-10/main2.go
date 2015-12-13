package main

import "fmt"

const times = 75

func main() {
	input := []byte{1, 1, 1, 3, 2, 2, 2, 1, 1, 3}
	var n []byte
	var l int
	var t byte

	for i := 0; i < times; i++ {
		n = make([]byte, 0, len(input) * 2)
		l = len(input)
		t = 1
		for j := 1; j < l; j++ {
			if input[j - 1] == input[j] {
				t++
			} else {
				n = append(n, t, input[j - 1])
				t = 1
			}
		}
		n = append(n, t, input[l - 1])

		input = n
	}

	fmt.Println(len(input))
}
