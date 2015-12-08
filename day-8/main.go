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
var encoded int

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
			l = findAndRemove(l, uniRe)
		}

		if quotRe.MatchString(l) {
			l = findAndRemove(l, quotRe)
		}

		if backslashRe.MatchString(l) {
			l = findAndRemove(l, backslashRe)
		}

		memory += len(l)
	}
	fmt.Printf("Chars: %d, Memory: %d, Answer ✩1: %d\n", chars, memory, chars-memory)

	// second star
	for _, l := range lines {
		l = mainRe.FindStringSubmatch(l)[1]

		if uniRe.MatchString(l) {
			l = findAndRemoveSpecial(l, uniRe, 5)
		}

		if quotRe.MatchString(l) {
			l = findAndRemoveSpecial(l, quotRe, 4)
		}

		if backslashRe.MatchString(l) {
			l = findAndRemoveSpecial(l, backslashRe, 4)
		}

		encoded += len(l) + 6 // 6 because "" -> "\"\"" and our l doesn't have the original
	}

	fmt.Printf("Encoded: %d, Chars: %d, Answer ✩2: %d\n", encoded, chars, encoded-chars)
}

func findAndRemoveSpecial(l string, re *regexp.Regexp, add int) string {
	matches := re.FindAllStringSubmatchIndex(l, -1)
	f, t := matches[0][0], matches[0][1]
	encoded += add

	if len(matches) > 1 {
		return findAndRemoveSpecial(l[0:f]+l[t:len(l)], re, add)
	}

	return l[0:f] + l[t:len(l)]
}

func findAndRemove(l string, re *regexp.Regexp) string {
	matches := re.FindAllStringSubmatchIndex(l, -1)
	f, t := matches[0][0], matches[0][1]
	memory++

	if len(matches) > 1 {
		return findAndRemove(l[0:f]+l[t:len(l)], re)
	}

	return l[0:f] + l[t:len(l)]
}
