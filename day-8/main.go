package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"regexp"
)

var mainRe = regexp.MustCompile(`^"(?P<val>.*)"$`)
var uniRe = regexp.MustCompile(`\\x[a-f0-9]{2}`)
var quotRe = regexp.MustCompile(`\\"`)
var backslashRe = regexp.MustCompile(`\\\\`)
var chars int
var memory int

func main() {
	contents, err := ioutil.ReadFile("input.data")
	if err != nil {
		panic(err)
	}

	lines := strings.Split(string(contents), "\n")

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

	fmt.Printf("Chars: %d, Memory: %d, Answer: %d\n", chars, memory, chars - memory)
}

func findAndRemove(l string, re *regexp.Regexp) string {
	matches := re.FindAllStringSubmatchIndex(l, -1)
	f, t := matches[0][0], matches[0][1]
	memory++

	if len(matches) > 1 {
		return findAndRemove(l[0:f] + l[t:len(l)], re)
	}

	return l[0:f] + l[t:len(l)]
}