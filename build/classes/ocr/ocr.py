from paddleocr import PaddleOCR, draw_ocr
import argparse
import re


def arg():
    parser = argparse.ArgumentParser(description='PaddleOCR')
    # model
    parser.add_argument("--img_path", type=str, default='C:\\Users\\Sarah\\Desktop\\Uni\\POS system\\POSSystem\\captured_frame.png', help="Path to image")
    args = parser.parse_args()
    return args


def ocr_run(img_path):
    # Paddleocr supports Chinese, English, French, German, Korean and Japanese
    # You can set the parameter `lang` as `ch`, `en`, `french`, `german`, `korean`, `japan`
    # to switch the language model in order
    ocr = PaddleOCR(use_angle_cls=True, lang='en') # need to run only once to download and load model into memory
    # img_path = 'C:\\Users\\Sarah\\Desktop\\Uni\\POS system\\POSSystem\\captured_frame.png'
    result = ocr.ocr(img_path, cls=True)
    # print(result)
    # print("---")

    for idx in range(len(result)):
        res = result[idx]
        for line in res:
            # print("-")
            # print(line[1][0])
            pattern_match = re.match(r'^\d{8}(-\d{4}){2}$', line[1][0])
            if pattern_match:
                return line[1][0]
            
    return None

#^\d{8}(-\d{4}){2}$

#^\d{8}(-\d{4}){3}-[a-z]{12}$


def main():
    args = arg()
    code_pattern = ocr_run(args.img_path)
    if code_pattern:
        print(f"Code pattern found: {code_pattern}")
    else:
        print("No code pattern found.")

if __name__ == '__main__':
    main()

