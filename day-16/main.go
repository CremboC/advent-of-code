package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"strconv"
)

type sue struct {
	id int
	props map[string]int
	proximity float64
}

func (s *sue) calculate() (proximity float64) {
	for prop, amount := range searchMap {
		if val, ok := s.props[prop]; ok {
			if val == amount {
				proximity += one / sMapLength
			}
			if val != amount {
				proximity -= one / sMapLength
			}
		}
	}
	s.proximity = proximity
	return proximity
}

func (s *sue) calculate2() (proximity float64) {
	for prop, amount := range searchMap {
		if val, ok := s.props[prop]; ok {
			switch prop {
			case "cats", "trees":
				if val > amount {
					proximity += one / sMapLength
				}
			case "goldfish", "pomeranians":
				if val < amount {
					proximity -= one / sMapLength
				}
			default:
				if val == amount {
					proximity += one / sMapLength
				}
				if val != amount {
					proximity -= one / sMapLength
				}
			}

		}
	}
	s.proximity = proximity
	return proximity
}

var sues = make([]*sue, 0)
var searchMap = map[string]int {
		"children": 3,
		"cats": 7,
		"samoyeds": 2,
		"pomeranians": 3,
		"akitas": 0,
		"vizslas": 0,
		"goldfish": 5,
		"trees": 3,
		"cars": 2,
		"perfumes": 1,
	}

var sMapLength = float64(len(searchMap))
var one = 1.0

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	rows := strings.Split(string(contents), "\n")

	for _, r := range rows {
		fs := strings.Split(r, " - ")
		who, props := fs[0], fs[1]

		m := make(map[string]int)
		for _, i := range strings.Split(props, ",") {
			ff := strings.Fields(strings.Trim(i, " "))
			m[ff[0]] = parseInt(ff[1])
		}

		sues = append(sues, &sue{
			parseInt(strings.Fields(who)[1]), m, 0,
		})
	}

	var top *sue
	for _, s := range sues {
		if top == nil || top.proximity < s.calculate2() {
			top = s
		}
	}

	fmt.Println(top)
}

func parseInt(str string) int {
	i, _ := strconv.Atoi(str)
	return i
}