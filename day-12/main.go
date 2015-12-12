package main

import (
	"fmt"
	"io/ioutil"
	"regexp"
	"strconv"
	"encoding/json"
)

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	one := starOne(&contents)
	two := starTwo(&contents)

	fmt.Printf("Star One %d, Star Two %d\n", one, two)
}

func starTwo(contents *[]byte) float64 {
	input := *contents
	var f interface{}
	var output float64
	json.Unmarshal(input, &f)

	output = rec(f)

	return output
}

func rec(f interface{}) (output float64) {
	outer:
	switch fv := f.(type) {
		case []interface{}:
			for _, val := range fv {
				output += rec(val)
			}
		case float64:
			output += fv
		case map[string]interface{}:
			for _, val := range fv {
				if val == "red" {
					break outer
				}
			}
			for _, val := range fv {
				output += rec(val)
			}
	}

	return output
}

func starOne(contents *[]byte) int {
	input := string(*contents)
	var output int

	regexp.MustCompile(`[\-0-9]+`).ReplaceAllStringFunc(input, func(match string) string {
		i, _ := strconv.Atoi(match)
		output += i
		return match
	})

	return output
}