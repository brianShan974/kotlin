import subprocess
from multiprocessing import Pool
import sys


concurrency = int(sys.argv[1])

params_list = [f"fuzzing_results_{i}" for i in range(1, 1 + concurrency)]

script_path = "fuzzing.sh"


def run_with_params(param):
    try:
        cmd = [
            "bash",
            script_path,
            param,
        ]

        result = subprocess.run(
            cmd,
            check=True,
            # text=True,
        )

        return {
            "params": param,
            "status": "success",
            "output": result.stdout.strip(),
        }
    except subprocess.TimeoutExpired:
        return {
            "params": param,
            "status": "timeout",
            "error": "Execution timed out",
        }
    except subprocess.CalledProcessError as e:
        return {
            "params": param,
            "status": "failed",
            "error": e.stderr.strip(),
        }


if __name__ == "__main__":
    with Pool(concurrency) as pool:
        results = pool.map(run_with_params, params_list)
