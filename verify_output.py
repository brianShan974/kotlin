from collections import Counter
import sys


exclusions = [
    "for-loop range must have an 'iterator()' method.",
    "expression 'step' of type 'int' cannot be invoked as a function. function 'invoke()' is not found.",
]
mappings = {
    "": "",
}


def get_diff(list1: list, list2: list) -> list:
    counter1 = Counter(list1)
    counter2 = Counter(list2)
    diff1 = counter1 - counter2
    diff2 = counter2 - counter1

    print(f"{counter1 = }", file=sys.stderr)
    print(f"{counter2 = }", file=sys.stderr)
    print(f"{diff1 = }", file=sys.stderr)
    print(f"{diff2 = }", file=sys.stderr)

    return list(diff1.elements()) + list(diff2.elements())


def get_file_output(file_name: str) -> list[str]:
    with open(file_name) as f:
        return list(
            filter(
                lambda line: line not in exclusions,
                map(
                    lambda line: mappings.get(line, line),
                    map(lambda line: line.strip(), f.readlines()),
                ),
            )
        )


def main():
    analysis_output = get_file_output(sys.argv[1])
    compiler_output = get_file_output(sys.argv[2])

    diff = get_diff(analysis_output, compiler_output)

    for line in diff:
        print(line)


if __name__ == "__main__":
    main()
