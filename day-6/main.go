package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
	// "math"
)

type Point struct {
	x, y int
}

type Instr struct {
	action string
	from   Point
	to     Point
}

var grid = make([][]bool, 1000)
var brightnessGrid = make([][]int, 1000)

func main() {
	dat, _ := ioutil.ReadFile("input.data")
	strs := strings.Split(string(dat), "\n")

	for i := range grid {
		grid[i] = make([]bool, 1000)
		brightnessGrid[i] = make([]int, 1000)
	}

	for _, str := range strs {
		inst := createInstr(str)
		for i := inst.from.x; i <= inst.to.x; i++ {
			for j := inst.from.y; j <= inst.to.y; j++ {
				doAction(inst.action, i, j)
				doBrightness(inst.action, i, j)
			}
		}
	}

	var lightsOn, brightness int
	for _, val := range grid {
		for _, v := range val {
			if v {
				lightsOn++
			}
		}
	}

	for _, val := range brightnessGrid {
		for _, v := range val {
			brightness += v
		}
	}

	fmt.Printf("Lights on: %d", lightsOn)
	fmt.Println()
	fmt.Printf("Total brightness: %d", brightness)
	fmt.Println()
}

func doBrightness(action string, x, y int) {
	switch action {
	case "toggle":
		brightnessGrid[x][y] += 2
	case "on":
		brightnessGrid[x][y]++
	case "off":
		if brightnessGrid[x][y] > 0 {
			brightnessGrid[x][y]--
		}
	}
}

func doAction(action string, x, y int) {
	switch action {
	case "toggle":
		grid[x][y] = !grid[x][y]
	case "on":
		grid[x][y] = true
	case "off":
		grid[x][y] = false
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
		from:   createPoint(split[fromIndex]),
		to:     createPoint(split[fromIndex+2]),
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
