import numpy as np


class Trainer:
    def __init__(self, features_file_path, labels_file_path):
        self.features = np.loadtxt(open(features_file_path, "rb"), delimiter=",")
        self.labels = np.loadtxt(open(labels_file_path, "rb"), delimiter=",")

        assert self.features.shape[0] == self.labels.shape[0]

    def iterate_minibatches(self, batchsize):
        for start_idx in range(0, self.features.shape[0] - batchsize + 1, batchsize):
            excerpt = slice(start_idx, start_idx + batchsize)
            yield self.features[excerpt], self.labels[excerpt]

    def get_all(self):
        return self.features, self.labels
