package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	// "strconv"
	// "math"
)

var vowels = []string{"a", "e", "i", "u", "o"}
var pairs = []string{"ab", "cd", "pq", "xy"}

func main() {
	dat, _ := ioutil.ReadFile("input.data")
	words := strings.Split(string(dat), "\n")

	var count int

	for _, word := range words {
		if hasInvalidPairs(word) {
			continue
		}

		if !hasThreeVowels(word) {
			continue
		}

		if !hasTwiceInRow(word) {
			continue
		}

		count++
	}

	fmt.Println(count)
}

func hasTwiceInRow(word string) bool {
	for i := 1; i < len(word); i++ {
		if word[i] == word[i - 1] {
			return true
		}
	}

	return false
}

func hasThreeVowels(word string) bool {
	var vowelCount int
	for _, v := range vowels {
		if c := strings.Count(word, v); c > 0 {
			vowelCount += c

			if (vowelCount >= 3) {
				return true
			}
		}
	}

	return vowelCount >= 3
}

func hasInvalidPairs(word string) bool {
	for _, p := range pairs {
		if strings.Contains(word, p) {
			return true
		}
	}

	return false
}