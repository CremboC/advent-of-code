package main

import (
	"fmt"
	"io/ioutil"
	// "math/rand"
	"strconv"
	"strings"
	"sync"

	"github.com/fighterlyt/permutation"
)

type Edge struct {
	distance int
}

type Route struct {
	path []string
	cost int
}

var wg sync.WaitGroup
var places = make(map[string]bool)
var edgeMap = make(map[string]*Edge)
var routes []Route

var rc = make(chan Route, 1000)

func main() {
	contents, err := ioutil.ReadFile("input.data")
	if err != nil {
		panic(err)
	}

	distances := strings.Split(string(contents), "\n")

	for _, distance := range distances {
		sp := strings.Split(distance, " ")
		from, to, distance := sp[0], sp[2], sp[4]

		edge := &Edge{
			distance: parseInt(distance),
		}

		edgeMap[from+to] = edge
		edgeMap[to+from] = edge

		places[from] = false
		places[to] = false
	}

	cities := make([]string, 0, len(places))
	for place := range places {
		cities = append(cities, place)
	}

	p, _ := permutation.NewPerm(cities, nil)

	go func() {
		for v := range rc {
			routes = append(routes, v)
		}
	}()

	n := p.Left() / 4
	for i := 1; i < 5; i++ {
		wg.Add(1)
		go next(p, n * i)
	}

	wg.Wait()

	minDistance, maxDistance := 100000, 0
	for _, r := range routes {
		if len(r.path) != len(cities) {
			continue
		}

		if r.cost < minDistance {
			minDistance = r.cost
		}

		if r.cost > maxDistance {
			maxDistance = r.cost
		}
	}

	fmt.Printf("Min: %d, Max: %d\n", minDistance, maxDistance)
}

func next(p *permutation.Permutator, n int) {
	defer wg.Done()
	nxn := p.NextN(n).([][]string)
	for _, ii := range nxn {
		// ii, _ := i.([]string)
		route := Route{}
		for j := 1; j < len(ii); j++ {
			from, to := ii[j - 1], ii[j]

			val, ok := edgeMap[from+to]
			if !ok {
				val, ok = edgeMap[to+from]
			}

			if !ok {
				route.path = append(route.path, to)
				rc <- route
				// routes = append(routes, route)
				break
			} else {
				route.cost += val.distance
				route.path = append(route.path, from)
			}

			if j == len(ii) - 1 {
				route.path = append(route.path, to)
				// routes = append(routes, route)
				rc <- route
			}
		}
	}
}

func parseInt(val string) int {
	i, _ := strconv.ParseInt(val, 10, 32)
	return int(i)
}
