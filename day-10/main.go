package main

import (
	"fmt"
	"regexp"
	"strconv"
)

var re = regexp.MustCompile(`1+|2+|3+|4+|5+|6+|7+|8+|9+`)

func main() {
	input := "1113222113"
	const times = 50

	for i := 0; i < times; i++ {
		matches := re.FindAllStringSubmatch(input, -1)
		input = ""

		for _, m := range matches {
			input += strconv.Itoa(len(m[0])) + string(m[0][0])
		}
	}

	fmt.Println(len(input))
}