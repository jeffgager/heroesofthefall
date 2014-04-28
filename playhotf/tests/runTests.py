import sys
import unittest

def main(sdk_path, test_path):
    sys.path.insert(0, sdk_path)
    import dev_appserver
    dev_appserver.fix_sys_path()
    suite = unittest.loader.TestLoader().discover(test_path, pattern='test*.py')
    unittest.TextTestRunner(verbosity=2).run(suite)


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print 'usage: runTests <sdk_path> <tests_path>'
    main(sys.argv[1], sys.argv[2])
