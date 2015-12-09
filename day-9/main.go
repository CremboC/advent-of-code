package main

import (
	"fmt"
	"io/ioutil"
	// "math/rand"
	"strconv"
	"strings"

	"github.com/fighterlyt/permutation"
)

type Edge struct {
	distance int
}

type Route struct {
	path []string
	cost int
}

var places = make(map[string]bool)
var edgeMap = make(map[string]*Edge)
var routes []Route

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

	for i, err := p.Next(); err == nil; i, err = p.Next() {
		ii, _ := i.([]string)
		route := Route{}
		for j := 1; j < len(ii); j++ {
			from, to := ii[j - 1], ii[j]

			val, ok := edgeMap[from+to]
			if !ok {
				val, ok = edgeMap[to+from]
			}

			if !ok {
				route.path = append(route.path, to)
				routes = append(routes, route)
				break
			} else {
				route.cost += val.distance
				route.path = append(route.path, from)
			}

			if j == len(ii) - 1 {
				route.path = append(route.path, to)
				routes = append(routes, route)
			}
		}
	}

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

func parseInt(val string) int {
	i, _ := strconv.ParseInt(val, 10, 32)
	return int(i)
}
