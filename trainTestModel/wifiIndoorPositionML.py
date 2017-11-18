from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import sys

import tensorflow as tf
import numpy as np
import trainer as TR

FLAGS = None

MODEL_EXPORT_PATH = "../model/"
MODEL_NAME = "wifi_position"
TEST_FEATURES_PATH = "../filtered_out/test/test_features"
TEST_LABELS_PATH = "../filtered_out/test/test_labels"
TRAIN_FEATURES_PATH = "../filtered_out/train/train_features"
TRAIN_LABELS_PATH = "../filtered_out/train/train_labels"

NUMBER_FEATURES = 8  # number of wifi access points
NUMBER_LABELS = 4  # locations
BATCH_SIZE = 128
NUM_EPOCHS = 1


def main(_):
    train_trainer = TR.Trainer(TRAIN_FEATURES_PATH, TRAIN_LABELS_PATH)
    test_trainer = TR.Trainer(TEST_FEATURES_PATH, TEST_LABELS_PATH)

    # Create the model
    x = tf.placeholder(tf.float32, [None, NUMBER_FEATURES], name="x")

    W = tf.Variable(tf.zeros([NUMBER_FEATURES, NUMBER_LABELS]), name="w")
    b = tf.Variable(tf.zeros([NUMBER_LABELS]), name="b")
    y_predicted = tf.add(tf.matmul(x, W, name="matmul"), b, name="add")
    tf.identity(y_predicted, name="y_placeholder")

    y_real = tf.placeholder(tf.float32, [None, NUMBER_LABELS], "y_real")

    cross_entropy = tf.reduce_mean(
        tf.nn.softmax_cross_entropy_with_logits(labels=y_real, logits=y_predicted, name="softmax"), name="reduce")

    train_step = tf.train.GradientDescentOptimizer(0.05).minimize(cross_entropy)

    sess = tf.InteractiveSession()
    tf.global_variables_initializer().run()

    # Train
    for i in range(NUM_EPOCHS):
        print("epoch: {}".format(i))
        for batch in train_trainer.iterate_minibatches(BATCH_SIZE):
            train_features, train_labels = batch
            sess.run(train_step, feed_dict={x: train_features, y_real: train_labels})

        # Test trained model
        test_features, test_labels = test_trainer.get_all()
        correct_prediction = tf.equal(tf.argmax(y_predicted, 1), tf.argmax(y_real, 1))
        accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
        print(sess.run(accuracy, feed_dict={x: test_features, y_real: test_labels}))

    # export
    export_graph = tf.Graph()
    with export_graph.as_default():

        export_input = tf.placeholder(tf.float32, [None, NUMBER_FEATURES], name="export_input")
        # freeze weights
        WC = tf.constant(W.eval(sess), name="weights_constant")
        BC = tf.constant(b.eval(sess), name="bias_constant")

        export_predicted = tf.add(tf.matmul(export_input, WC, name="export_matmul"), BC, name="export_add")
        OUTPUT = tf.nn.softmax(export_predicted, name="export_output")

        export_sess = tf.Session()
        init = tf.initialize_all_variables()
        export_sess.run(init)

        graph_def = export_graph.as_graph_def()
        tf.train.write_graph(graph_def, MODEL_EXPORT_PATH, MODEL_NAME, as_text=False)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--test_features', type=str, default=TEST_FEATURES_PATH,
                        help='location of the test features')
    parser.add_argument('--test_labels', type=str, default=TEST_LABELS_PATH,
                        help='location of the test labels')
    parser.add_argument('--train_features', type=str, default=TRAIN_FEATURES_PATH,
                        help='location of the train features')
    parser.add_argument('--train_labels', type=str, default=TRAIN_LABELS_PATH,
                        help='location of the train labels')
    FLAGS, unparsed = parser.parse_known_args()
tf.app.run(main=main, argv=[sys.argv[0]] + unparsed)
