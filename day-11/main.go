package main

import "fmt"

var password = []byte("hepxcrrq")
// var password = []byte("hepxxyzz")

type pair struct {
	i, j int
}

func (p *pair) overlap(op pair) bool {
	return p.i == op.i || p.i == op.j || p.j == op.i || p.j == op.j
}

func main() {
	for {
		if increasingOrder(password) && pairAppearingTwice(password) && doesntIncludeIllegal(password) {
			break
		}

		password = incrementPassword(password)
	}

	fmt.Println(string(password))
}

const min = 97 // a
const max = 122 // z

func incrementPassword(password []byte) []byte {
	for i := len(password) - 1; i >= 0; i-- {
		nval := password[i] + 1
		if nval <= max {
			password[i] = nval
			break
		}
		if nval > max  {
			password[i] = min
		}
	}

	return password
}

func doesntIncludeIllegal(word []byte) bool {
	for _, letter := range word {
		switch letter {
			case 105, 111, 108:
			return false
		}
		// if letter == 105 || letter == 111 || letter == 108 {
			// return false
		// }
	}

	return true
}

// with no overlap
func pairAppearingTwice(word []byte) bool {
	length := len(word)
	ps := make(map[string][]*pair)
	for i := 1; i < length; i++ {
		if word[i] == word[i - 1] {
			str := string([]byte{word[i], word[i - 1]})
			ap := &pair{i, i - 1}
			val, _ := ps[str];
			ps[str] = append(val, ap)
		}
	}

	if len(ps) == 0 {
		return false
	}

	if len(ps) > 1 {
		return true
	}

	if len(ps) == 1 {
		for _, pairs := range ps {
			for _, p := range pairs {
				for _, p2 := range pairs {
					if &p != &p2 && !p.overlap(*p2) {
						return true
					}
				}
			}
		}
	}

	return false
}

func increasingOrder(word []byte) bool {
	length := len(word)
	for i := 2; i < length; i++ {
		first, second, third := word[i - 2], word[i - 1], word[i]

		if third - second == 1 && second - first == 1 {
			return true
		}
	}

	return false
}