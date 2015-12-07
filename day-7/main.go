package main

import (
	"fmt"
	"io/ioutil"
	"strconv"
	"strings"
)

var opts = []string{
	"AND", "OR", "LSHIFT", "RSHIFT", "NOT",
}

var registers = make(map[string]uint16)

func main() {
	contents, err := ioutil.ReadFile("test.data")
	if err != nil {
		panic(err)
	}
	cmds := strings.Split(string(contents), "\n")

	for _, cmd := range cmds {
		left, operator, target := decon(cmd)

		switch operator {
		case "":
			val, ok := parseInt(left)
			if !ok {
				f := strings.TrimSpace(left)
				registers[target] = registers[f]
			} else {
				registers[target] = val
			}
		case "NOT":
			sp := strings.Split(left, "NOT ")
			registers[target] = ^registers[sp[1]]
		default:
			l, r := split(left, operator)
			switch operator {
			case "AND":
				lval, lok := parseInt(l)
				rval, rok := parseInt(r)

				switch {
				case rok && lok:
					registers[target] = lval & rval
				case !rok && lok:
					registers[target] = lval & registers[r]
				case rok && !lok:
					registers[target] = registers[l] & rval
				case !rok && !lok:
					registers[target] = registers[l] & registers[r]
				}

			case "OR":
				lval, lok := parseInt(l)
				rval, rok := parseInt(r)

				switch {
				case rok && lok:
					registers[target] = lval | rval
				case !rok && lok:
					registers[target] = lval | registers[r]
				case rok && !lok:
					registers[target] = registers[l] | rval
				case !rok && !lok:
					registers[target] = registers[l] | registers[r]
				}
			case "LSHIFT":
				val, _ := parseInt(r)
				registers[target] = registers[l] << val
			case "RSHIFT":
				val, _ := parseInt(r)
				registers[target] = registers[l] >> val
			}
		}
	}

	fmt.Println(registers)
	fmt.Printf("Register a: %d", registers["a"])
	fmt.Println()
}

func split(left, operator string) (string, string) {
	sp := strings.Split(left, " "+operator+" ")
	return sp[0], sp[1]
}

func decon(cmd string) (string, string, string) {
	sp := strings.Split(cmd, " -> ")
	operator := ""
	for _, op := range opts {
		if strings.Contains(cmd, op) {
			operator = op
			break
		}
	}

	return sp[0], operator, sp[1]
}

func parseInt(val string) (uint16, bool) {
	i, err := strconv.ParseInt(val, 10, 16)
	return uint16(i), err == nil
}
