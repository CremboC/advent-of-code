package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"strconv"
	"math"
)

func main() {
    dat, _ := ioutil.ReadFile("input.data")
	data := string(dat)

	boxes := strings.Split(data, "\n")
	var totalArea int

	for _, dims := range boxes {
		l, w, h := getDimensions(dims)
		current := getArea(l, w, h) + smallest(l * w, w * h, h * l)
		fmt.Printf("%d, %d, %d .. %d", l, w, h, current)
		fmt.Println()
		totalArea = totalArea + current
	}

	fmt.Println(totalArea)
}

func getArea(l, w, h int) int {
	return (2 * l * w) + (2 * w * h) + (2 * h * l);
}

func getDimensions(dims string) (l, w, h int) {
	split := strings.Split(dims, "x")
	l, _ = strconv.Atoi(split[0])
	w, _ = strconv.Atoi(split[1])
	h, _ = strconv.Atoi(split[2])
	return
}

func smallest(l, w, h int) int {
	return int(math.Min(float64(l), math.Min(float64(w), float64(h))))
}