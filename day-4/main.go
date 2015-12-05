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

		if hasXZeroes(hash, 6) {
			fmt.Printf("%s, %d, %s", input, number, hash)
			fmt.Println()
			break
		}

		number++
		input = original
	}
}

func hasXZeroes(hash string, num int) bool {
	for i := 0; i < num; i++ {
		if string(hash[i]) != "0" {
			return false;
		}
	}

	return true;
}