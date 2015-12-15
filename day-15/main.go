package main

import (
    "fmt"
    "io/ioutil"
    "strings"
)

const maxingr = 100

type Cookie struct {
    capacity int
    durability int
    flavor int
    texture int
}

type Ingr struct {
    name string
    capacity int
    durability int
    flavor int
    texture int
}

func (c *Cookie) score() int {
    return c.capacity * c.durability * c.flavor * c.texture
}

var ingrs = make(map[string]*Ingr)

func main() {
    contents, _ := ioutil.ReadFile("test.data")
    rows := strings.Split(string(contents), "\n")

    for _, r := range rows {
        var name string
        var capacity, durability, flavor, texture, calories int
        fmt.Println(r)
        n, err := fmt.Sscanf(r, "%s: capacity %d, durability %d, flavor %d, texture %d, calories %d", &name, &capacity, &durability, &flavor, &texture, &calories)
        if err != nil {
            panic(err)
        }
        fmt.Println(n)

        ingrs[name] = &Ingr{name, capacity, durability, flavor, texture}
        fmt.Println(ingrs[name])
    }

    fmt.Println(ingrs)
}