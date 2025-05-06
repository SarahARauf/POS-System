import easyocr
reader = easyocr.Reader(['en'], gpu=False) # this needs to run only once to load the model into memory
result = reader.readtext('C:\\Users\\Sarah\\Desktop\\Uni\\POS system\\POSSystem\\captured_frame.png', detail = 0)
print(result)