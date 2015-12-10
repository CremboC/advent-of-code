package main

import (
	"fmt"
	"regexp"
	"strconv"
	"bytes"
)

var re = regexp.MustCompile(`1+|2+|3+|4+|5+|6+|7+|8+|9+`)

func main() {
	input := "1113222113"
	const times = 50
	var buffer bytes.Buffer

	for i := 0; i < times; i++ {
		matches := re.FindAllStringSubmatch(input, -1)
		buffer.Reset()

		for _, m := range matches {
			buffer.WriteString(strconv.Itoa(len(m[0])))
			buffer.WriteByte(m[0][0])
		}
		input = buffer.String()
	}

	fmt.Println(len(input))
}