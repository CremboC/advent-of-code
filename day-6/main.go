package main

import (
	"fmt"
	"io/ioutil"
	"strings"
	"strconv"
	// "math"
)

type Point struct {
	x, y int
}

type Instr struct {
	action string
	from Point
	to Point
}

var grid = make([][]bool, 1000)

func main() {
	dat, _ := ioutil.ReadFile("input.data")
	strs := strings.Split(string(dat), "\n")

	// instructions := make([]Instr, 0, len(strs))

	for i := range grid {
		grid[i] = make([]bool, 1000)
	}

	for _, str := range strs {
		inst := createInstr(str)
		for i := inst.from.x; i <= inst.to.x; i++ {
			for j := inst.from.y; j <= inst.to.y; j++ {
				doAction(inst.action, i, j)
			}
		}
	}

	var lightsOn int
	for _, val := range grid {
		for _, v := range val {
			if v {
				lightsOn++
			}
		}
	}

	fmt.Println(lightsOn)
}

func doAction(action string, x, y int) {
	switch action {
		case "toggle": grid[x][y] = !grid[x][y]
		case "on": grid[x][y] = true
		case "off": grid[x][y] = false
	}
}

func createInstr(str string) Instr {
	fromIndex := 2
	actionIndex := 1
	split := strings.Split(str, " ")
	if strings.Contains(str, "toggle") {
		fromIndex = 1
		actionIndex = 0
	}

	return Instr{
		action: split[actionIndex],
		from: createPoint(split[fromIndex]),
		to: createPoint(split[fromIndex + 2]),
	}
}

func createPoint(str string) Point {
	sp := strings.Split(str, ",")
	return Point{parseInt(sp[0]), parseInt(sp[1])}
}

func parseInt(str string) int {
	i, _ := strconv.ParseInt(str, 10, 0)
	return int(i)
}