package main

import (
	"fmt"
	"crypto/md5"
	"encoding/hex"
	// "strings"
	"strconv"
	// "math"
)

func main() {
	const original string = "iwrupvqb"
    input := original
	var number int

	for {
		input += strconv.Itoa(number)
		hasher := md5.New()
		hasher.Write([]byte(input))
		hash := hex.EncodeToString(hasher.Sum(nil))

		fmt.Printf("%s, %d, %s", input, number, hash)
		fmt.Println()

		// fmt.Println(hash)
		if isFiveZeroes(hash) {
			fmt.Println(hash)
			break
		}

		number++
		input = original
	}
}

func isFiveZeroes(hash string) bool {
	for i := 0; i < 5; i++ {
		if string(hash[i]) != "0" {
			return false;
		}
	}

	return true;
}