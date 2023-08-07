package com.amazonaws.kda.flinkchecker;

public abstract class Check {
    abstract CheckResult check(CheckParams params);
}
