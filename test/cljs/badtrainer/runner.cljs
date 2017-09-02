(ns badtrainer.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [badtrainer.core-test]))

(doo-tests 'badtrainer.core-test)
