import glob
import csv 
import ntpath


input_path = input("please provide input path")
output_path = input("Please provide output path....")

files = glob.glob(input_path + "/sample-coinbase-data-*")


for file_path in files:

	file_name = ntpath.basename(file_path)

	with open(file_path, 'r') as in_file:
		with open(output_path + '/' + 'corrected-' + file_name, 'w') as out_file:
			reader = csv.reader(in_file)
			writer = csv.writer(out_file)

			for coinbase in reader:
				coinbase_id = coinbase[0]
				writer.writerow([coinbase_id, 'COINBASE'])

