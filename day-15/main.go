package main

import (
	"fmt"
	"io/ioutil"
	"math"
	"strings"
)

const maxingr = 100

type Cookie struct {
	ingredients map[string]int
	score       int
}

type Influence struct {
	capacityInf   float64
	durabilityInf float64
	flavorInf     float64
	textureInf    float64
}

type Ingr struct {
	name       string
	capacity   int
	durability int
	flavor     int
	texture    int
	calories   int
	*Influence
}

func (c *Cookie) calculate() (int, int) {
	capacityScore, durabilityScore, flavorScore, textureScore, totalCalories := 0, 0, 0, 0, 0
	for ingredient, amount := range c.ingredients {
		capacityScore += amount * ingrs[ingredient].capacity
		durabilityScore += amount * ingrs[ingredient].durability
		flavorScore += amount * ingrs[ingredient].flavor
		textureScore += amount * ingrs[ingredient].texture
        totalCalories += amount * ingrs[ingredient].calories
	}
    // fmt.Println(capacityScore, durabilityScore, flavorScore, textureScore)
	c.score = floor(capacityScore) * floor(durabilityScore) * floor(flavorScore) * floor(textureScore)
	return c.score, totalCalories
}

func NewInf() *Influence {
	return &Influence{0, 0, 0, 0}
}

func (i Influence) top() float64 {
	return math.Max(i.capacityInf, math.Max(i.durabilityInf, math.Max(i.flavorInf, i.textureInf)))
}

func floor(num int) int {
	if num < 0 {
		return 0
	}
	return num
}

var ingrs = make(map[string]*Ingr)

func main() {
	contents, _ := ioutil.ReadFile("input.data")
	rows := strings.Split(string(contents), "\n")

	var maxCap, maxDura, maxFlavor, maxTexture *Ingr
	for _, r := range rows {
		var name string
		var capacity, durability, flavor, texture, calories int
		fmt.Sscanf(r, "%s capacity %d, durability %d, flavor %d, texture %d, calories %d", &name, &capacity, &durability, &flavor, &texture, &calories)
		ingrs[name] = &Ingr{name, capacity, durability, flavor, texture, calories, NewInf()}
		if maxCap == nil || capacity > maxCap.capacity {
			maxCap = ingrs[name]
		}
		if maxDura == nil || durability > maxDura.durability {
			maxDura = ingrs[name]
		}
		if maxFlavor == nil || flavor > maxFlavor.flavor {
			maxFlavor = ingrs[name]
		}
		if maxTexture == nil || texture > maxTexture.texture {
			maxTexture = ingrs[name]
		}
	}

	total := maxCap.capacity + maxDura.durability + maxFlavor.flavor + maxTexture.texture
	for _, ingr := range ingrs {
		ingr.capacityInf = float64(ingr.capacity) / float64(total)
		ingr.durabilityInf = float64(ingr.durability) / float64(total)
		ingr.flavorInf = float64(ingr.flavor) / float64(total)
		ingr.textureInf = float64(ingr.texture) / float64(total)
		t := ingr.capacityInf + ingr.durabilityInf + ingr.flavorInf + ingr.textureInf
		fmt.Println(ingr.name, ingr.Influence, t)
	}

	fmt.Println(maxCap.name, maxCap.top())
	fmt.Println(maxDura.name, maxDura.top())
	fmt.Println(maxFlavor.name, maxFlavor.top())
	fmt.Println(maxTexture.name, maxTexture.top())

    var maxVal, ma, mb, mc, md int
	for a := 1; a <= 100; a++ {
		for b := 1; b <= 100-a; b++ {
			for c := 1; c <= 100-a; c++ {
				for d := 1; d <= 100-a; d++ {
					cookie := &Cookie{
						map[string]int{
							"Frosting":     a,
							"Candy":        b,
							"Butterscotch": c,
							"Sugar":        d,
						},
						0,
					}

                    if a + b + c + d == 100 {
                        val, calories := cookie.calculate()
                        if val > maxVal && calories == 500 {
                            maxVal = val
                            ma, mb, mc, md = a, b, c, d
                        }
                    }
				}
			}
		}
	}

    fmt.Println(maxVal, ma, mb, mc, md)

	// cookie := &Cookie{
	// 	map[string]int{
	// 		"Frosting":     25,
	// 		"Candy":        31,
	// 		"Butterscotch": 28,
	// 		"Sugar":        16,
	// 	},
	// 	0,
	// }
	// fmt.Println(cookie.calculate())
}
