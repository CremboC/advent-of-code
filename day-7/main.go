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

type Op struct {
	op1, op2, operator, target string
}

var registers = make(map[string]uint16)
var operations = make(map[string][]Op)

func main() {
	contents, err := ioutil.ReadFile("input.data")
	if err != nil {
		panic(err)
	}
	cmds := strings.Split(string(contents), "\n")
	operations = make(map[string][]Op, len(cmds))

	for _, cmd := range cmds {
		op1, op2, operator, target := decon(cmd)
		operations[target] = append(operations[target], Op{op1, op2, operator, target})
	}

	// first star
	calculate("a")
	fmt.Printf("Register a: %d\n", registers["a"])

	// second star
	for key := range registers {
		delete(registers, key)
	}
	for _, cmd := range cmds {
		op1, op2, operator, target := decon(cmd)
		operations[target] = append(operations[target], Op{op1, op2, operator, target})
	}
	put(Op{op1: "956", target: "b"})
	calculate("a")

	fmt.Printf("Register a: %d\n", registers["a"])
}

func calculate(target string) {
	ops := operations[target]

	var o Op
	if len(ops) > 0 {
		o = ops[0]
		operations[target] = append(ops[:0], ops[1:]...)
	}

	switch o.operator {
	case "NOT": not(o)
	case "LSHIFT": lshift(o)
	case "RSHIFT": rshift(o)
	case "OR": or(o)
	case "AND": and(o)
	default: put(o)
	}
}

func put(o Op) {
	var lval uint16
	var lok bool

	i, err := strconv.ParseInt(o.op1, 10, 16)
	if err == nil {
		lval = uint16(i)
		lok = true
	} else {
		lval, lok = registers[o.op1]
	}

	if !lok {
		calculate(o.op1)
		lval = registers[o.op1]
	}
	registers[o.target] = lval
}

func not(o Op) {
	val, ok := registers[o.op1]
	if !ok {
		calculate(o.op1)
		val = registers[o.op1]
	}
	registers[o.target] = ^val
}

func lshift(o Op) {
	val, ok := registers[o.op1]
	if !ok {
		calculate(o.op1)
		val = registers[o.op1]
	}
	registers[o.target] = val << parseInt(o.op2)
}

func rshift(o Op) {
	val, ok := registers[o.op1]
	if !ok {
		calculate(o.op1)
		val = registers[o.op1]
	}
	registers[o.target] = val >> parseInt(o.op2)
}

func and(o Op) {
	var lval uint16
	var lok bool

	i, err := strconv.ParseInt(o.op1, 10, 16)
	if err == nil {
		lval = uint16(i)
		lok = true
	} else {
		lval, lok = registers[o.op1]
	}
	rval, rok := registers[o.op2]

	if !lok {
		calculate(o.op1)
		lval = registers[o.op1]
	}

	if !rok {
		calculate(o.op2)
		rval = registers[o.op2]
	}

	registers[o.target] = lval & rval
}

func or(o Op) {
	lval, lok := registers[o.op1]
	rval, rok := registers[o.op2]

	if !lok {
		calculate(o.op1)
		lval = registers[o.op1]
	}

	if !rok {
		calculate(o.op2)
		rval = registers[o.op2]
	}

	registers[o.target] = lval | rval
}

func split(left, operator string) (string, string) {
	sp := strings.Split(left, " "+operator+" ")
	return sp[0], sp[1]
}

func decon(cmd string) (op1, op2, operator, target string) {
	sp := strings.Split(cmd, " -> ")
	for _, op := range opts {
		if strings.Contains(cmd, op) {
			operator = op
			break
		}
	}

	left := strings.Split(sp[0], " ")
	switch operator {
	case "NOT":
		op1 = left[1]
	case "LSHIFT":
		fallthrough
	case "RSHIFT":
		fallthrough
	case "OR":
		fallthrough
	case "AND":
		op1, op2 = left[0], left[2]
	default:
		op1 = left[0]
	}
	target = sp[1]

	return
}

func parseInt(val string) uint16 {
	i, _ := strconv.ParseInt(val, 10, 16)
	return uint16(i)
}