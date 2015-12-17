package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"sort"
)

var boxes []int

const size = 150

var mask []bool
var masks []Mask

type Mask struct {
	m []bool
}

func (m *Mask) add(v bool) bool {
	if len(m.m) == len(mask) {
		return false
	}
	m.m = append(m.m, v)
	return true
}

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	for _, r := range strings.Split(string(contents), "\n") {
		var size int
		fmt.Sscanf(r, "%d", &size)
		boxes = append(boxes, size)
	}
	sort.Ints(boxes)

	mask = make([]bool, len(boxes))

	var m, i uint
	N := uint(1) << uint(len(mask))
	outer:
	for m = 0; m < N; m++ {
		msk := Mask{}
		for i = 0; i < m; i++ {
			if m>>i & 1 == 1 {
				if !msk.add(true) {
					masks = append(masks, msk)
					continue outer
				}
			} else if m>>i & 1 == 0  {
				if !msk.add(false) {
					masks = append(masks, msk)
					continue outer
				}
			}
		}
	}

	var count, minCount int
	min := 1000
	for _, m := range masks {
		var collection []int
		for j, b := range boxes {
			if m.m[j] {
				collection = append(collection, b)
			}
		}
		if sum(collection) == size {
			if len(collection) < min {
				min = len(collection)
				minCount = 0
			}

			if len(collection) == min {
				minCount++
			}
			count++
		}
	}

	fmt.Printf("Star one: %d, Star two: %d\n", count, minCount)
}

func sum(s []int) (sum int) {
	for _, i := range s {
		sum += i
	}
	return
}