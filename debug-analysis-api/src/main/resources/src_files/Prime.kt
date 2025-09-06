fun isPrime(num: Int): Boolean {
    if (num < 2) return false

    if (num == 2 || num == 3) return true

    if (num % 2 == 0 || num % 3 == 0) return false

    var i = 5
    while (i * i <= num) {
        if (num % i == 0 || num % (i + 2) == 0) {
            return false
        }
        i += 6
    }

    return true
}
