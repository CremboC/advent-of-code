package main

import "testing"

func BenchmarkMain10(b *testing.B) {
	// run the Fib function b.N times
	for n := 0; n < b.N; n++ {
		main()
	}
}