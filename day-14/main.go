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
	points   int
}

type Timing struct {
	time     int
	name     string
	distance int
}

func (r *Reindeer) fly(time <-chan int, leaderch chan<- *Timing) {
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

		leaderch <- &Timing{t, r.name, r.distance}
	}
}

var reindeers = make(map[string]*Reindeer)
var scoreboard = make(map[int][]*Timing)

const maxt = 2503

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	rows := strings.Split(string(contents), "\n")

	for _, r := range rows {
		var who string
		var speed, time, rest int
		fmt.Sscanf(r, "%s can fly %d km/s for %d seconds, but then must rest for %d seconds.", &who, &speed, &time, &rest)
		reindeers[who] = &Reindeer{who, speed, time, rest, 0, 0}
	}

	leaderch := make(chan *Timing)
	chs := make(map[string]chan int, len(reindeers))

	wg.Add(len(reindeers))
	for _, reindeer := range reindeers {
		chs[reindeer.name] = make(chan int)
		go reindeer.fly(chs[reindeer.name], leaderch)
	}

	wg.Add(1)
	go listenToLeader(leaderch)

	for t := 1; t < maxt; t++ {
		for _, ch := range chs {
			ch <- t
		}
	}
	for _, ch := range chs {
		close(ch)
	}

	wg.Wait()

	// star 1
	var topDeer *Reindeer
	for _, r := range reindeers {
		if topDeer == nil || r.distance > topDeer.distance {
			topDeer = r
		}
	}
	fmt.Printf("✩ Star ✩ one ✩: %s, %dkm\n", topDeer.name, topDeer.distance)

	// star 2
	topDeer = nil
	for _, r := range reindeers {
		if topDeer == nil || r.points > topDeer.points {
			topDeer = r
		}
	}

	fmt.Printf("✩ Star ✩ two ✩: %s, %d points\n", topDeer.name, topDeer.points)
}

func listenToLeader(ch chan *Timing) {
	defer wg.Done()
	rx := make(map[int]int)
	for t := range ch {
		rx[t.time]++
		val, _ := scoreboard[t.time]
		scoreboard[t.time] = append(val, t)

		if len(scoreboard[t.time]) == len(reindeers) {
			go calculatePoints(t.time)
		}

		if t.time == maxt - 1 && rx[t.time] == len(reindeers) {
			break
		}
	}
}

func calculatePoints(time int) {
	var leader *Timing
	for _, t := range scoreboard[time] {
		if leader == nil || t.distance > leader.distance {
			leader = t
		}
	}

	for _, t := range scoreboard[time] {
		if t.distance == leader.distance {
			reindeers[t.name].points++
		}
	}
}

func parseInt(str string) int {
	i, _ := strconv.Atoi(str)
	return i
}
