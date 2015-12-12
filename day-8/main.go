package main

import (
	"fmt"
	"io/ioutil"
	"regexp"
	"strings"
)

// first star
var mainRe = regexp.MustCompile(`^"(?P<val>.*)"$`)
var uniRe = regexp.MustCompile(`\\x[a-f0-9]{2}`)
var quotRe = regexp.MustCompile(`\\"`)
var backslashRe = regexp.MustCompile(`\\\\`)
var chars int
var memory int

// second star
var encoded, add int

func main() {
	contents, err := ioutil.ReadFile("input.data")
	if err != nil {
		panic(err)
	}

	lines := strings.Split(string(contents), "\n")

	// first star
	for _, l := range lines {
		chars += len(l)
		l = mainRe.FindStringSubmatch(l)[1]

		if uniRe.MatchString(l) {
			l = uniRe.ReplaceAllStringFunc(l, empty)
		}

		if quotRe.MatchString(l) {
			l = quotRe.ReplaceAllStringFunc(l, empty)
		}

		if backslashRe.MatchString(l) {
			l = backslashRe.ReplaceAllStringFunc(l, empty)
		}

		memory += len(l)
	}
	fmt.Printf("Chars: %d, Memory: %d, Answer ✩1: %d\n", chars, memory, chars-memory)

	// second star
	for _, l := range lines {
		l = mainRe.FindStringSubmatch(l)[1]

		if uniRe.MatchString(l) {
			add = 5
			l = uniRe.ReplaceAllStringFunc(l, empty2)
		}

		if quotRe.MatchString(l) {
			add = 4
			l = quotRe.ReplaceAllStringFunc(l, empty2)
		}

		if backslashRe.MatchString(l) {
			add = 4
			l = backslashRe.ReplaceAllStringFunc(l, empty2)
		}

		encoded += len(l) + 6 // 6 because "" -> "\"\"" and our l doesn't have the original
	}

	fmt.Printf("Encoded: %d, Chars: %d, Answer ✩2: %d\n", encoded, chars, encoded-chars)
}

func empty(match string) string {
	memory++
	return ""
}

func empty2(match string) string {
	encoded += add
	return ""
}