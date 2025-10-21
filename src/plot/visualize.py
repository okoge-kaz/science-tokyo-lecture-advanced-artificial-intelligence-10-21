import argparse
from pathlib import Path
from typing import List, Tuple
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


def read_csv_clean(path: Path) -> pd.DataFrame:
    df = pd.read_csv(path)
    if "NoOfEvals" not in df.columns:
        raise ValueError(f"{path} must include a 'NoOfEvals' column. Found columns: {list(df.columns)}")
    # Coerce to numeric and drop NaNs
    df["NoOfEvals"] = pd.to_numeric(df["NoOfEvals"], errors="coerce")
    before = len(df)
    df = df.dropna(subset=["NoOfEvals"]).copy()
    if len(df) < before:
        print(f"[WARN] Dropped {before - len(df)} rows with non-numeric NoOfEvals in {path.name}")
    # Sort and deduplicate
    df = df.sort_values("NoOfEvals").drop_duplicates(subset=["NoOfEvals"], keep="last").reset_index(drop=True)

    # Diagnostics
    if len(df) == 0:
        print(f"[WARN] {path.name}: empty after cleaning.")
    else:
        n_unique = df["NoOfEvals"].nunique()
        min_x = df["NoOfEvals"].min()
        max_x = df["NoOfEvals"].max()
        all_zero = bool((df["NoOfEvals"] == 0).all())
        print(f"[INFO] {path.name}: rows={len(df)} unique_x={n_unique} range=[{min_x}, {max_x}] all_zero={all_zero}")
        if n_unique == 1:
            print(f"[WARN] {path.name}: NoOfEvals has only one unique value.")
    return df


def trial_columns(df: pd.DataFrame) -> List[str]:
    return [c for c in df.columns if c != "NoOfEvals"]


def per_trial_xy(df: pd.DataFrame, col: str, rebase: bool, use_index: bool) -> Tuple[np.ndarray, np.ndarray]:
    y = pd.to_numeric(df[col], errors="coerce")
    mask = y.notna()
    y = y[mask]
    if use_index:
        x = pd.RangeIndex(start=0, stop=len(y), step=1)
    else:
        x = df.loc[mask, "NoOfEvals"].astype(float).copy()
        if rebase and len(x) > 0:
            x = x - float(x.iloc[0])
    return x.to_numpy(), y.to_numpy()


def compute_global_limits(datasets, logy: bool, mean_only: bool, rebase_per_trial: bool, x_as_index: bool):
    """
    datasets: list of tuples (label, df)
    Returns (xmin, xmax, ymin, ymax) across all plotted series.
    """
    xmin, xmax = np.inf, -np.inf
    ymin, ymax = np.inf, -np.inf

    for label, df in datasets:
        cols = trial_columns(df)
        if not cols:
            continue

        if mean_only:
            if x_as_index:
                aligned = df[cols].apply(pd.to_numeric, errors="coerce")
                mean_series = aligned.mean(axis=1, skipna=True)
                x = np.arange(len(mean_series))
                y = mean_series.to_numpy()
            else:
                aligned = df[["NoOfEvals"] + cols].copy()
                aligned[cols] = aligned[cols].apply(pd.to_numeric, errors="coerce")
                x = aligned["NoOfEvals"].astype(float).to_numpy()
                y = aligned[cols].mean(axis=1, skipna=True).to_numpy()
            # update limits
            if len(x) > 0:
                xmin = min(xmin, np.nanmin(x))
                xmax = max(xmax, np.nanmax(x))
            if len(y) > 0:
                if logy:
                    ypos = y[y > 0]
                    if len(ypos) > 0:
                        ymin = min(ymin, np.nanmin(ypos))
                        ymax = max(ymax, np.nanmax(ypos))
                else:
                    ymin = min(ymin, np.nanmin(y))
                    ymax = max(ymax, np.nanmax(y))
        else:
            for col in cols:
                x, y = per_trial_xy(df, col, rebase=rebase_per_trial, use_index=x_as_index)
                if len(x) > 0:
                    xmin = min(xmin, np.nanmin(x))
                    xmax = max(xmax, np.nanmax(x))
                if len(y) > 0:
                    if logy:
                        ypos = y[y > 0]
                        if len(ypos) > 0:
                            ymin = min(ymin, np.nanmin(ypos))
                            ymax = max(ymax, np.nanmax(ypos))
                    else:
                        ymin = min(ymin, np.nanmin(y))
                        ymax = max(ymax, np.nanmax(y))

            # also consider mean if show_mean was desired later (but global scale should reflect only what we draw)
            # Here mean_only determines we don't include means when not requested.

    # Fallbacks if nothing valid
    if not np.isfinite(xmin) or not np.isfinite(xmax) or xmin == xmax:
        xmin, xmax = 0.0, 1.0
    if not np.isfinite(ymin) or not np.isfinite(ymax) or ymin == ymax:
        ymin, ymax = (1e-3, 1.0) if logy else (0.0, 1.0)

    return float(xmin), float(xmax), float(ymin), float(ymax)


def plot_single_method(
    df: pd.DataFrame,
    label: str,
    xmin: float,
    xmax: float,
    ymin: float,
    ymax: float,
    logy: bool,
    mean_only: bool,
    show_mean: bool,
    rebase_per_trial: bool,
    x_as_index: bool,
    output_path: Path,
):
    plt.figure(figsize=(11, 7))
    cols = trial_columns(df)

    if not mean_only:
        # plot each trial
        default_markers = ["o", "s", "^", "D", "v", "x", "P", "*", "h", "<", ">"]
        for i, col in enumerate(cols):
            m = default_markers[i % len(default_markers)]
            x, y = per_trial_xy(df, col, rebase=rebase_per_trial, use_index=x_as_index)
            plt.plot(x, y, marker=m, linewidth=1.6, markersize=4.5, alpha=0.6, label=f"Trial {i + 1}")

    if show_mean or mean_only:
        if x_as_index:
            aligned = df[cols].apply(pd.to_numeric, errors="coerce")
            mean_series = aligned.mean(axis=1, skipna=True)
            x_mean = np.arange(len(mean_series))
            plt.plot(x_mean, mean_series.to_numpy(), linewidth=3.0, alpha=0.98, label="Mean")
        else:
            aligned = df[["NoOfEvals"] + cols].copy()
            aligned[cols] = aligned[cols].apply(pd.to_numeric, errors="coerce")
            plt.plot(
                aligned["NoOfEvals"].astype(float).to_numpy(),
                aligned[cols].mean(axis=1, skipna=True).to_numpy(),
                linewidth=3.0,
                alpha=0.98,
                label="Mean",
            )

    if logy:
        plt.yscale("log")

    # Axis labels
    if x_as_index:
        plt.xlabel("Step Index per Trial", fontsize=12)
    else:
        xlabel = (
            "Number of Evaluations (per trial rebased)"
            if rebase_per_trial and not mean_only
            else "Number of Evaluations"
        )
        plt.xlabel(xlabel, fontsize=12)

    plt.ylabel("Best Value (Log Scale)" if logy else "Best Value", fontsize=12)
    plt.title(f"{label}", fontsize=14)
    plt.grid(True, which="both", ls="-", alpha=0.3)
    plt.xlim(xmin, xmax)
    # For log scale, ensure ymin>0
    if logy and ymin <= 0:
        ymin = max(ymin, 1e-12)
    plt.ylim(ymin, ymax)
    plt.legend(loc="best")
    output_path.parent.mkdir(parents=True, exist_ok=True)
    plt.tight_layout()
    plt.savefig(output_path, dpi=300, bbox_inches="tight")
    plt.close()


def main():
    parser = argparse.ArgumentParser(description="Output separate figures per method with shared scales.")
    parser.add_argument("--files", nargs="+", required=True, help="CSV file paths (2 or more).")
    parser.add_argument("--labels", nargs="*", default=None, help="Labels matching files.")
    parser.add_argument("--no-logy", action="store_true", help="Disable log scale on y-axis.")
    parser.add_argument("--mean-only", action="store_true", help="Draw only per-method mean curves.")
    parser.add_argument("--show-mean", action="store_true", help="Also draw mean with trials (ignored if --mean-only).")
    parser.add_argument(
        "--rebase-per-trial", action="store_true", help="Rebase X so each trial starts at 0 evals (for trials)."
    )
    parser.add_argument("--x-as-index", action="store_true", help="Use per-trial row index as X (0..k-1).")
    parser.add_argument("--output-dir", default="figures/separate", help="Directory to write per-method figures.")
    args = parser.parse_args()

    files = [Path(f) for f in args.files]
    labels = args.labels if (args.labels and len(args.labels) == len(files)) else [f.stem for f in files]

    # Load all
    datasets = [(label, read_csv_clean(f)) for label, f in zip(labels, files)]

    # Global limits across what we intend to draw
    xmin, xmax, ymin, ymax = compute_global_limits(
        datasets,
        logy=not args.no_logy,
        mean_only=args.mean_only,
        rebase_per_trial=args.rebase_per_trial,
        x_as_index=args.x_as_index,
    )
    print(f"[INFO] Global limits -> X:[{xmin}, {xmax}]  Y:[{ymin}, {ymax}]  (logy={not args.no_logy})")

    outdir = Path(args.output_dir)
    for label, df in datasets:
        outfile = outdir / f"{label.replace(' ', '_')}.png"
        plot_single_method(
            df=df,
            label=label,
            xmin=xmin,
            xmax=xmax,
            ymin=ymin,
            ymax=ymax,
            logy=not args.no_logy,
            mean_only=args.mean_only,
            show_mean=args.show_mean,
            rebase_per_trial=args.rebase_per_trial,
            x_as_index=args.x_as_index,
            output_path=outfile,
        )
        print(f"[OK] Wrote {outfile}")


if __name__ == "__main__":
    main()
