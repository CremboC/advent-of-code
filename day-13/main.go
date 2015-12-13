package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
	"sort"

	"github.com/fighterlyt/permutation"
)

var people = make(map[string]map[string]int)
var ps = make([]string, 0)
var ch = make(chan int, 4)

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	rows := strings.Split(string(contents), "\n")

	for _, r := range rows {
		sp := strings.Split(r, " ")
		p, sign, amount, t := sp[0], sp[2], sp[3], strings.Split(sp[10], ".")[0]

		if _, ok := people[p]; !ok {
			ps = append(ps, p)
			people[p] = make(map[string]int)
		}

		i, _ := strconv.Atoi(amount)
		if sign == "gain" {
			people[p][t] = i
		} else {
			people[p][t] = -i
		}
	}

	// first star
	star()

	// second star
	ps = append(ps, "Me")
	people["Me"] = make(map[string]int)
	for k, v := range people {
		v["Me"] = 0
		people["Me"][k] = 0
	}
	star()
}

func star() {
	p, _ := permutation.NewPerm(ps, nil)

	n := p.Left() / 4
	for i := 1; i < 5; i++ {
		go next(p, n*i)
	}
	arr := []int{<-ch, <-ch, <-ch, <-ch}
	sort.Ints(arr)
	fmt.Printf("Optimal happiness: %d\n", arr[len(arr) - 1])
}

func next(p *permutation.Permutator, n int) {
	nxn := p.NextN(n).([][]string)
	max := -1000000
	for _, sitting := range nxn {
		length := len(sitting)
		var happiness int

		for i := 0; i < length; i++ {
			switch i {
			case 0:
				happiness += people[sitting[i]][sitting[length-1]]
				happiness += people[sitting[i]][sitting[i+1]]
			case length - 1:
				happiness += people[sitting[i]][sitting[0]]
				happiness += people[sitting[i]][sitting[length-2]]
			default:
				happiness += people[sitting[i]][sitting[i-1]]
				happiness += people[sitting[i]][sitting[i+1]]
			}
		}
		if happiness > max {
			max = happiness
		}
	}
	ch <- max
}
