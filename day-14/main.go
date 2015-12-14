package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
	"sync"
)

var wg sync.WaitGroup

type Reindeer struct {
	name     string
	speed    int
	flytime  int
	rest     int
	distance int
}

func (r *Reindeer) fly(time <-chan int) {
	defer wg.Done()
	nextRest := r.flytime
	nextFly := r.flytime + r.rest
	for t := range time {
		if t > nextRest && t <= nextFly {
			// do nothing
		} else {
			r.distance += r.speed
		}

		if t == nextFly {
			nextRest = t + r.flytime
			nextFly = t + r.flytime + r.rest
		}
	}
}

var reindeers = make(map[string]*Reindeer)

const maxt = 2503

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	rows := strings.Split(string(contents), "\n")

	for _, r := range rows {
		f := strings.Fields(r)
		who, speed, time, rest := f[0], parseInt(f[3]), parseInt(f[6]), parseInt(f[13])

		reindeers[who] = &Reindeer{who, speed, time, rest, 0}
	}

	chs := make(map[string]chan int, len(reindeers))
	wg.Add(len(reindeers))
	for _, reindeer := range reindeers {
		chs[reindeer.name] = make(chan int)
		go reindeer.fly(chs[reindeer.name])
	}

	for t := 1; t < maxt; t++ {
		for _, ch := range chs {
			ch <- t
		}
	}
	for _, ch := range chs {
		close(ch)
	}

	wg.Wait()

	for _, r := range reindeers {
		fmt.Printf("%+v\n", r)
	}
}

func parseInt(str string) int {
	i, _ := strconv.Atoi(str)
	return i
}
