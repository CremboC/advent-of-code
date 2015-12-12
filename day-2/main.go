package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"strconv"
	"math"
	"sort"
)

func main() {
    dat, _ := ioutil.ReadFile("input.data")
	boxes := strings.Split(string(dat), "\n")
	var totalArea, totalRibbon int

	for _, dims := range boxes {
		l, w, h := getDimensions(dims)
		current := getArea(l, w, h) + smallest(l * w, w * h, h * l)

		totalRibbon += getRibbon(l, w, h)
		totalArea += current
	}

	fmt.Printf("Total area: %d\n", totalArea)
	fmt.Printf("Total ribbon: %d\n", totalRibbon)
}

func getRibbon(l, w, h int) int {
	arr := []int{l, w, h}
	sort.Ints(arr)
	return (arr[0] * 2 + arr[1] * 2) + (l * w * h) // wrap + bow
}

func getArea(l, w, h int) int {
	return (2 * l * w) + (2 * w * h) + (2 * h * l);
}

func getDimensions(dims string) (int, int, int) {
	split := strings.Split(dims, "x")
	return atoi(split[0]), atoi(split[1]), atoi(split[2])
}

func atoi(str string) int {
	i, _ := strconv.Atoi(str)
	return i
}

func smallest(l, w, h int) int {
	return int(math.Min(float64(l), math.Min(float64(w), float64(h))))
}