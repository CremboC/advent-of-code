package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	// "strconv"
	// "math"
)

type pair struct {
	i, j int
	str string
}

func (p *pair) overlap(op pair) bool {
	return p.i == op.i || p.i == op.j || p.j == op.i || p.j == op.j
}

var vowels = []string{"a", "e", "i", "u", "o"}
var pairs = []string{"ab", "cd", "pq", "xy"}

func main() {
	dat, _ := ioutil.ReadFile("input.data")
	words := strings.Split(string(dat), "\n")

	var count int

	for _, word := range words {

		// if matchesFirstStarRules(word) {
		// 	count++
		// }

		if matchesSecondStarRules(word) {
			count++
			fmt.Println(word)
		}
	}

	fmt.Println(count)
}

func matchesFirstStarRules(word string) bool {
	if hasInvalidPairs(word) {
		return false
	}

	if !hasThreeVowels(word) {
		return false
	}

	if !hasTwiceInRow(word) {
		return false
	}

	return true
}

func matchesSecondStarRules(word string) bool {
	if !repeatsWithInBetween(word) {
		return false
	}

	if !pairAppearingTwice(word) {
		return false
	}

	return true
}

// with no overlap
func pairAppearingTwice(word string) bool {
	ps := make(map[string]pair)
	for i := 1; i < len(word); i++ {
		ap := pair{i, i - 1, string(word[i]) + string(word[i - 1])}
		val, ok := ps[ap.str];

		if ok && !val.overlap(ap) {
			return true
		}

		ps[ap.str] = ap
	}

	return false
}

func repeatsWithInBetween(word string) bool {
	if len(word) < 3 {
		return false
	}

	for i := 2; i < len(word); i++ {
		if word[i] == word[i - 2] {
			return true
		}
	}

	return false
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