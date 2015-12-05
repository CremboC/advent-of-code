package main

import (
	"fmt"
	"io/ioutil"
	// "strings"
	// "strconv"
	// "math"
)

func main() {
	dat, _ := ioutil.ReadFile("input.data")
	data := string(dat)

	const max = 10000
	var houses [max][max]int
	var x, y int = max / 2, max / 2
	var x1, y1 int = max / 2, max / 2

	houses[x][y] = 1

	for i := 0; i < len(data); i++ {
		move := string(data[i])

		if i % 2 == 0 {
			x, y = change(x, y, move)
		} else {
			x1, y1 = change(x1, y1, move)
		}

		houses[x][y]++
		houses[x1][y1]++
	}

	var visited int
	for i := 0; i < max; i++ {
		for j := 0; j < max; j++ {
			if houses[i][j] > 0 {
				visited++
			}
		}
	}

	fmt.Printf("Visited: %d", visited)
	fmt.Println()
}

func change(x, y int, move string) (int, int) {
	if move == "v" {
		y--
	}

	if move == "^" {
		y++
	}

	if move == ">" {
		x++
	}

	if move == "<" {
		x--
	}

	return x, y
}
